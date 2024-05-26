import pandas

def store_csv(df, name):
	df.to_csv("csv/" + name + ".csv", index = False)
