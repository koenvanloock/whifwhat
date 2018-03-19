import React from 'react'
import AppBar from 'material-ui/AppBar'
import FlatButton from 'material-ui/FlatButton'
import AppBarMenu from "./AppBarMenu"

class Navbar extends React.Component {

    constructor(props) {
        super(props);
        this.tournamentName = props.tournamentName;
    }


  render() {
    return (
        <div>
          <AppBar
              title="Whiff What"
              iconElementLeft={<AppBarMenu />}
              iconElementRight={<FlatButton label={this.tournamentName} />}
          />
        </div>
    )

  }

}

export default Navbar
