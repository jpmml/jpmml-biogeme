import joblib
import pandas

def store_csv(df, name):
	df.to_csv("csv/" + name + ".csv", index = False)

def store_pkl(obj, name):
	joblib.dump(obj, "pkl/" + name + ".pkl")
