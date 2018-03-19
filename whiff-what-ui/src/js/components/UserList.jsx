import React, {Component} from "react";
import axios from "axios";

class UserList extends Component {

  constructor(props) {
    super(props);
    this.state = {
      players: []
    }
  }

  componentDidMount() {
    axios.get("http://localhost:9000/players", {
      headers: {
        'Content-Type': 'application/json'
      }
    })
        .then(res => {
          const players = res.data.map(obj => obj);
          this.setState({players});
        });
  }

  render() {
    return (<div>
      <ul>
        {this.state.players.map(user => <li key={user.id.toString()}>{user.firstname + " " + user.lastname}</li>)}
      </ul>
    </div>)
  }
}

export default UserList