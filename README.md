JPMML-Biogeme [![Build Status](https://github.com/jpmml/jpmml-biogeme/workflows/maven/badge.svg)](https://github.com/jpmml/jpmml-biogeme/actions?query=workflow%3A%22maven%22)
=============

Java library and command-line application for converting [Biogeme](https://biogeme.epfl.ch/) discrete choice models to PMML.

# Installation #

Enter the project root directory and build using [Apache Maven](https://maven.apache.org/):

```
mvn clean install
```

The build produces a library JAR file `pmml-biogeme/target/pmml-biogeme-1.0-SNAPSHOT.jar`, and an executable uber-JAR file `pmml-biogeme-example/target/pmml-biogeme-example-executable-1.0-SNAPSHOT.jar`.

# Usage #

A typical workflow can be summarized as follows:

1. Use Python to conduct a Biogeme experiment.
2. Construct a `dict` holding the Biogeme model and the Biogeme estimation results, and save it in `pickle` data format to a file in local filesystem.
3. Use the JPMML-Biogeme command-line application to turn the Pickle file to a PMML file.

### The Python side of operations

Conducting a Biogeme experiment:

```python
from biogeme.biogeme import BIOGEME
from biogeme.models import loglogit

import joblib

V, availability, Choice = scenario()

model = loglogit(V, availability, Choice)

# Save the model component
# The internal state of the model is modified during the BIOGEME.estimate() method call, rendering it un-pickleable later on
joblib.dump(model, "_model.pkl")

biogeme = BIOGEME(database, model)

results = biogeme.estimate()

# Save the estimation results component
joblib.dump(results, "_results.pkl")
```

Constructing a `dict` holding the Biogeme model and the Biogeme estimation results:

```python
import joblib

experiment = {
	"model" : joblib.load("_model.pkl"),
	"results" : joblib.load("_results.pkl")
}

joblib.dump(experiment, "experiment.pkl")
```

### The JPMML-Biogeme side of operations

Converting the Biogeme experiment Pickle file `experiment.pkl` to a PMML file `experiment.pmml`:

```
java -jar pmml-biogeme-example/target/pmml-biogeme-example-executable-1.0-SNAPSHOT.jar --pkl-input experiment.pkl --pmml-output experiment.pmml
```

Getting help:

```
java -jar pmml-biogeme-example/target/pmml-biogeme-example-executable-1.0-SNAPSHOT.jar --help
```

# License #

JPMML-Biogeme is licensed under the terms and conditions of the [GNU Affero General Public License, Version 3.0](https://www.gnu.org/licenses/agpl-3.0.html).

If you would like to use JPMML-Biogeme in a proprietary software project, then it is possible to enter into a licensing agreement which makes JPMML-Biogeme available under the terms and conditions of the [BSD 3-Clause License](https://opensource.org/licenses/BSD-3-Clause) instead.

# Additional information #

JPMML-Biogeme is developed and maintained by Openscoring Ltd, Estonia.

Interested in using [Java PMML API](https://github.com/jpmml) software in your company? Please contact [info@openscoring.io](mailto:info@openscoring.io)