import React from "react";
import Counter from "./Counter"
import UserList from "./UserList"
import CreateTournamentForm from "./CreateTournamentForm"
import CreateTournamentFlow from "./CreateTournamentFlow"
import {BrowserRouter, Link, Route} from "react-router-dom";

class RouterViewComponent extends React.Component {

  constructor(props) {
    super(props);
  }

  render() {
    return (
          <div id="content">
            <Route exact path="/count" component={Counter} />
            <Route exact path="/players" component={UserList} />
            <Route exact path="/form" component={CreateTournamentFlow}/>
          </div>
    );
  }

}

export default RouterViewComponent;