import React, {Component} from "react"
import {Stepper, Step, StepLabel, StepContent} from "material-ui/Stepper"
import RaisedButton from "material-ui/RaisedButton"
import FlatButton from "material-ui/FlatButton"
import CreateTournamentForm from "./CreateTournamentForm";

class CreateTournamentFlow extends Component {

  constructor(props){
    super(props);
    this.state = {
      finished: false,
      stepIndex: 0,
    };
    this.handleNext = this.handleNext.bind(this)
    this.handlePrev = this.handlePrev.bind(this)
  }

  handleNext() {
    console.log("")
    const stepIndex = this.state.stepIndex;
    this.setState({
      stepIndex: stepIndex + 1,
      finished: stepIndex >= 2,
    });
  }

  handlePrev() {
    const {stepIndex} = this.state;
    if (stepIndex > 0) {
      this.setState({stepIndex: stepIndex - 1});
    }
  }

    getStepContent(stepIndex) {
        switch (stepIndex) {
            case 0:
                return (<CreateTournamentForm/>);
            case 1:
                return 'What is an ad group anyways?';
            case 2:
                return 'This is the bit I really care about!';
            default:
                return 'You\'re a long way from home sonny jim!';
        }
    }

  renderStepActions(step) {
    const stepIndex = this.state.stepIndex;

    return (
        <div style={{margin: '12px 0'}}>
          <RaisedButton
              label={stepIndex === 2 ? 'Finish' : 'Next'}
              disableTouchRipple={true}
              disableFocusRipple={true}
              primary={true}
              onClick={this.handleNext}
              style={{marginRight: 12}}
          />
          {step > 0 && (
              <FlatButton
                  label="Back"
                  disabled={stepIndex === 0}
                  disableTouchRipple={true}
                  disableFocusRipple={true}
                  onClick={this.handlePrev}
              />
          )}
        </div>
    );
  }


  render() {
    const {finished, stepIndex} = this.state;

    return (
        <div style={{maxWidth: 380, maxHeight: 400, margin: 'auto'}}>
          <Stepper activeStep={stepIndex} orientation="horizontal">
            <Step>
              <StepLabel>Tournament general</StepLabel>
            </Step>
            <Step>
              <StepLabel>Series options</StepLabel>
              <StepContent>
                <p>An ad group contains one or more ads which target a shared set of keywords.</p>
                {this.renderStepActions(1)}
              </StepContent>
            </Step>
            <Step>
              <StepLabel>Create an ad</StepLabel>
              <StepContent>
                <p>
                  Try out different ad text to see what brings in the most customers,
                  and learn how to enhance your ads using features like ad extensions.
                  If you run into any problems with your ads, find out how to tell if
                  they're running and how to resolve approval issues.
                </p>
                {this.renderStepActions(2)}
              </StepContent>
            </Step>
          </Stepper>
            <div>{this.getStepContent(stepIndex)}</div>
          {finished && (
              <p style={{margin: '20px 0', textAlign: 'center'}}>
                <a
                    href="#"
                    onClick={(event) => {
                      event.preventDefault();
                      this.setState({stepIndex: 0, finished: false});
                    }}
                >
                  Click here
                </a> to reset the example.
              </p>
          )}
        </div>
    );
  }

}

export default CreateTournamentFlow