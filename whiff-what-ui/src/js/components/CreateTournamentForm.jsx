import React, {Component} from "react";
import TextField from "material-ui/TextField/TextField";
import RaisedButton from "material-ui/RaisedButton";
import DatePicker from "material-ui/DatePicker";
import CheckBox from "material-ui/Checkbox";
import Paper from "material-ui/Paper";
import {Toolbar, ToolbarTitle} from "material-ui/Toolbar";
import axios from "axios";
import addTournament from '../state/actions/creationTournamentFlowActions'


class CreateTournamentForm extends Component {

  constructor(props) {
    super(props);
    this.state = {
      tournamentName: "",
      tournamentDate: new Date(),
      hasMultipleSeries: false,
      maximumNumberOfSeriesEntries: 1,
      showClub: false
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleDateChange = this.handleDateChange.bind(this);
    this.changeMultipleSeries = this.changeMultipleSeries.bind(this);
  }

  paddedNr(number) {
    return (number < 10
            ? "0"
            : "") + number;
  }

  handleChange(event) {
    this.setState({tournamentName: event.target.value});
  }

  handleDateChange(obj, newDate) {
    this.setState({tournamentDate: newDate});
  }

  handleSubmit(event) {
    console.log(this.state);
    let date = this.state.tournamentDate;
    var postObj = {
      tournamentName: this.state.tournamentName,
      hasMultipleSeries: this.state.hasMultipleSeries,
      showClub: this.state.showClub,
      maximumNumberOfSeriesEntries: this.state.maximumNumberOfSeriesEntries
    };
    postObj.tournamentDate = date.getFullYear() + "-" + this.paddedNr(date.getMonth() + 1) + "-" + this.paddedNr(date.getDate());
    axios.post("http://localhost:9000/tournaments", postObj)
        .then(response => {
            dispatch(addTournament(postObj));
            console.log(response.data)
        })
  }

  changeMultipleSeries(obj, newVal) {
    this.setState({hasMultipleSeries: newVal});
  }

  render() {
    return (
        <Paper>
          <Toolbar>
            <ToolbarTitle text="Maak een nieuw tornooi"/>
          </Toolbar>
          <div className="default-padding">
              <TextField
                  id="text-field-default"
                  floatingLabelText="Tornooinaam"
                  value={this.state.tournamentName}
                  onChange={this.handleChange}/>

              <DatePicker
                  hintText="Tornooidatum"
                  value={this.state.tournamentDate}
                  onChange={this.handleDateChange}
              />
              <CheckBox
                  value={this.state.hasMultipleSeries}
                  label="Verschillende reeksen"
                  onCheck={this.changeMultipleSeries}
              />

              <RaisedButton
                  secondary={true}
                  label="Create"
                  onClick={this.handleSubmit}/>
          </div>
        </Paper>
    )
  }

}

export default CreateTournamentForm