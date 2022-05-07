# On-screen keyboards with predictive lighting

This project aims to measure any change in typing speed or accuracy relevant to visual alterations of the on-screen keyboard.  The primary independent variable is application of lighting to the on-screen keyboard showing the most likely keys the user may want to press next.  The effect of this will be measured by both accuracy and speed of the user.  Accuracy will be correlated in two ways, a raw score based on right/wrong keys (with flexibility in case of accidental extra inputs) and an adjusted accuracy measurement which factors in physical distance between expected and entered keystokes.

For a more functional overview see [About.md](about.md)

# Relevant links:

   - [Summary Video](https://www.youtube.com/watch?v=QbgB-r6luGA) ([Download](https://cs.colostate.edu/~bdvision/short-presentation.mp4))
   - [Longer Project Presentation](https://www.youtube.com/watch?v=0Zkmo593qnc) ([Download](https://cs.colostate.edu/~bdvision/long-presentation.mp4))
   - [Website](https://cs.colostate.edu/~bdvision): Contains some additional high-level description and a live demonstration of the experiment
   - [Technical Documentation](https://github.com/csu-hci-projects/Badvision/blob/main/about.md): Description of the architecture, design, and coding details
   - [Project demonstration](https://cs.colostate.edu/~bdvision): Live demonstration of full project
   - [Progress Report video](https://youtu.be/kpJdo-KhyiI): This shows the prototype in action and explains the experiment structure

# Source repository structure

This project is divided into separate modules:

- Experiment : This contains the website sources, images, and data files for the experiment itself.  Deploy this directly to your CS home folder and ensure the permissions are correct (755) so that visitors can access it.  There is nothing else to do but browse to the html page.  Before posting, you shoud review the script residing in your own cgi-bin folder to understand how it works first.

- MarkovGenerator : This contains the Markov chain predictor generator, the output of which goes into the experiment.  This is run one-time from a python notebook.  I used VSCode but any Python development tool that understand the ipynb notebook format should work.  This uses the files in the training folder to produce the phrases.js predictive typing model.

- Training : This contains all the training data for the predictor in one file and the validation text in another file.  At the moment the validation text is not used, I had just collected it in case I decided to use an AI trained model instead of the statistical Markov model.  The training.txt file is the same as the phrases.js content.  If you do not modify these files you can just use the already-generated phrases.js file.

- DataAnalysis : This utility parses the collected JSON data files and produces a summary of the data as an excel workbook.  More information about this is in the  [About.md](about.md) file.

- Docs : Report (LaTeX and PDF) and collected data files are here.
