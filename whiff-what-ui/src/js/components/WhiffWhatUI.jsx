import React from "react";
import RouterViewComponent from "./RouterViewComponent";
import NavBar from "./Navbar";

class WhiffWhatUI extends React.Component {
  constructor(props) {
    super(props);
    this.currentTournament = {tournamentName: store.tournament.tournamentName};
  }


  render() {
    return (
        <div>
          <NavBar tournamentName={this.currentTournament.tournamentName}/>
          <RouterViewComponent/>
        </div>
    );
  }


}
export default WhiffWhatUI;
