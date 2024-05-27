from biogeme.biogeme import BIOGEME
from biogeme.expressions import Numeric, Variable
from biogeme.models import logit, loglogit
from pandas import DataFrame

import copy
import joblib
import numpy

from common import store_csv
from optima import database, scenario

V, _, Choice, _ = scenario()

# Preserve the initial state of the utility function set for DCM pickling purposes
V_orig = copy.deepcopy(V)

def store_dcm(V, betas, availability, name):
	dcm = {
		"V" : V,
		"betas" : betas,
		"availability" : availability
	}

	joblib.dump(dcm, "pkl/" + name + ".pkl")

def estimate(model_func, name):
	model = model_func()

	biogeme = BIOGEME(database, model)
	biogeme.generate_html = False
	biogeme.generate_pickle = False
	biogeme.saveIterations = False
	biogeme.modelName = name

	results = biogeme.estimate()

	return results.getBetaValues()

def predict(proba_func, betas):
	prediction = DataFrame()

	choices = list(V.keys())

	for choice in choices:
		proba = proba_func(choice)
		prediction["probability({})".format(choice)] = proba.getValue_c(betas = betas, database = database, prepareIds = True)

	prediction["Choice"] = [choices[idx] for idx in numpy.argmax(prediction.values, axis = 1)]

	# Move the choice column to the first position
	cols = prediction.columns.tolist()
	prediction = prediction[cols[-1:] + cols[:-1]]

	return prediction

availability = None

betas = estimate(lambda: loglogit(V, availability, Choice), "MNLOptima")
store_dcm(V_orig, betas, availability, "MNLOptima")

prediction = predict(lambda x: logit(V, availability, x), betas)
store_csv(prediction, "MNLOptima")

availability = {
	0 : Numeric(1),
	1 : Variable("AV_CAR"),
	2 : Numeric(1)
}

store_dcm(V_orig, betas, availability, "MNLAvOptima")

prediction = predict(lambda x: logit(V, availability, x), betas)
store_csv(prediction, "MNLAvOptima")