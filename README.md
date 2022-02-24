# On-screen keyboards with predictive lighting

This project aims to measure any change in typing speed or accuracy relevant to visual alterations of the on-screen keyboard.  The primary independent variable is application of lighting to the on-screen keyboard showing the most likely keys the user may want to press next.  The effect of this will be measured by both accuracy and speed of the user.  Accuracy will be correlated in two ways, a raw score based on right/wrong keys (with flexibility in case of accidental extra inputs) and an adjusted accuracy measurement which factors in physical distance between expected and entered keystokes.

# Source repository structure

This project is divided into modules with their own intended purposes:

- Experiment : This contains the website sources, images, and data files for the experiment itself

- MarkovGenerator : This contains the Markov chain predictor generator, the output of which goes into the experiment

- Training : This contains all the training data for the predictor in one file and the validation text in another file