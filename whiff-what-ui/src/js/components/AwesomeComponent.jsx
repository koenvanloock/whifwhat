import React from "react";
import Counter from "./Counter"
import UserList from "./UserList"
import {BrowserRouter, Link, Route} from "react-router-dom";

class AwesomeComponent extends React.Component {

  constructor(props) {
    super(props);
  }

  render() {
    return (
          <div id="content">
            <Route exact path="/count" component={Counter} />
            <Route exact path="/players" component={UserList} />
          </div>
    );
  }

}

export default AwesomeComponent;