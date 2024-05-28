from biogeme.biogeme import BIOGEME
from biogeme.expressions import Numeric, Variable
from biogeme.models import logit, loglogit
from pandas import DataFrame

import copy
import numpy

from common import store_csv, store_pkl
from optima import database, scenario

V, _, Choice, _ = scenario()

def estimate(model_func):
	model = model_func()

	# Make a deep copy of the model object.
	# The BIOGEME.estimate() method appears to tamper with the utility function set of the model object, making it un-pickleable.
	model_copy = copy.deepcopy(model)

	biogeme = BIOGEME(database, model_copy)
	biogeme.generate_html = False
	biogeme.generate_pickle = False
	biogeme.saveIterations = False
	biogeme.modelName = None

	results = biogeme.estimate()

	return {
		"model" : model,
		"betas" : results.getBetaValues()
	}

def predict(proba_func, experiment):
	prediction = DataFrame()

	choices = list(V.keys())

	for choice in choices:
		proba = proba_func(choice)
		prediction["probability({})".format(choice)] = proba.getValue_c(betas = experiment["betas"], database = database, prepareIds = True)

	prediction["Choice"] = [choices[idx] for idx in numpy.argmax(prediction.values, axis = 1)]

	# Move the choice column to the first position
	cols = prediction.columns.tolist()
	prediction = prediction[cols[-1:] + cols[:-1]]

	return prediction

availability = None

experiment = estimate(lambda: loglogit(V, availability, Choice))
store_pkl(experiment, "MNLOptima")

prediction = predict(lambda x: logit(V, availability, x), experiment)
store_csv(prediction, "MNLOptima")

availability = experiment["model"].av

availability[1] = Variable("AV_CAR")

store_pkl(experiment, "MNLAvOptima")

prediction = predict(lambda x: logit(V, availability, x), experiment)
store_csv(prediction, "MNLAvOptima")