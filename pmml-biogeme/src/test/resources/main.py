from biogeme.biogeme import BIOGEME
from biogeme.database import Database
from biogeme.expressions import Beta, Expression, Variable
from biogeme.models import loglogit
from pandas import DataFrame
from scipy.special import softmax

import copy
import joblib
import numpy
import pandas

df = pandas.read_csv("dat/optima.dat", sep = "\t")
#print(df.shape)

database = Database("optima", df)

#
# See prepareData.py
#

# %%
# Variables from the data
Choice = Variable('Choice')
TimePT = Variable('TimePT')
TimeCar = Variable('TimeCar')
MarginalCostPT = Variable('MarginalCostPT')
CostCarCHF = Variable('CostCarCHF')
distance_km = Variable('distance_km')
Gender = Variable('Gender')
OccupStat = Variable('OccupStat')
Weight = Variable('Weight')

# %%
# Exclude observations such that the chosen alternative is -1
database.remove(Choice == -1.0)

# %%
# Normalize the weights
sum_weight = database.data['Weight'].sum()
number_of_rows = database.data.shape[0]
normalized_weight = Weight * number_of_rows / sum_weight

#
# See prepareScenario.py
#

# %%
# List of parameters to be estimated.
ASC_CAR = Beta('ASC_CAR', 0, None, None, 0)
ASC_PT = Beta('ASC_PT', 0, None, None, 1)
ASC_SM = Beta('ASC_SM', 0, None, None, 0)
BETA_TIME_FULLTIME = Beta('BETA_TIME_FULLTIME', 0, None, None, 0)
BETA_TIME_OTHER = Beta('BETA_TIME_OTHER', 0, None, None, 0)
BETA_DIST_MALE = Beta('BETA_DIST_MALE', 0, None, None, 0)
BETA_DIST_FEMALE = Beta('BETA_DIST_FEMALE', 0, None, None, 0)
BETA_DIST_UNREPORTED = Beta('BETA_DIST_UNREPORTED', 0, None, None, 0)
BETA_COST = Beta('BETA_COST', 0, None, None, 0)

# %%
# Definition of variables:
# For numerical reasons, it is good practice to scale the data to
# that the values of the parameters are around 1.0.
TimePT_scaled = TimePT / 200
TimeCar_scaled = TimeCar / 200
CostCarCHF_scaled = CostCarCHF / 10
distance_km_scaled = distance_km / 5
male = Gender == 1
female = Gender == 2
unreportedGender = Gender == -1

fulltime = OccupStat == 1
notfulltime = OccupStat != 1

factor = 1.0

marginal_cost_scenario = MarginalCostPT * factor
marginal_cost_pt_scaled = marginal_cost_scenario / 10

# Definition of utility functions:
v_pt = (
	ASC_PT
	+ BETA_TIME_FULLTIME * TimePT_scaled * fulltime
	+ BETA_TIME_OTHER * TimePT_scaled * notfulltime
	+ BETA_COST * marginal_cost_pt_scaled
)
v_car = (
	ASC_CAR
	+ BETA_TIME_FULLTIME * TimeCar_scaled * fulltime
	+ BETA_TIME_OTHER * TimeCar_scaled * notfulltime
	+ BETA_COST * CostCarCHF_scaled
)
v_sm = (
	ASC_SM
	+ BETA_DIST_MALE * distance_km_scaled * male
	+ BETA_DIST_FEMALE * distance_km_scaled * female
	+ BETA_DIST_UNREPORTED * distance_km_scaled * unreportedGender
)

# Associate utility functions with the numbering of alternatives
V = {0: v_pt, 1: v_car, 2: v_sm}

#
# See nestedLogitModel.py
#

# Preserve the original utility function set 
V_orig = copy.deepcopy(V)

df.to_csv("csv/Optima.csv", index = False)

logprob = loglogit(V, None, Choice)
#print(logprob)

biogeme = BIOGEME(database, logprob)
biogeme.generate_html = False
biogeme.generate_pickle = False
biogeme.saveIterations = False
biogeme.modelName = "MNLOptima"

results = biogeme.estimate()
#print(results)

betas = results.getBetaValues()

dcm = {
	"V" : V_orig,
	"betas" : betas
}

joblib.dump(dcm, "pkl/MNLOptima.pkl")

utilityValues = {k : v.getValue_c(betas = betas, database = database) for (k, v) in V.items()}
#print(utilityValues)

prediction = numpy.array(tuple(utilityValues.values())).T
prediction = numpy.apply_along_axis(softmax, 1, prediction)
prediction = DataFrame(prediction, columns = ["probability({})".format(k) for k in utilityValues.keys()])
#print(prediction)

prediction.to_csv("csv/MNLOptima.csv", index = False)
