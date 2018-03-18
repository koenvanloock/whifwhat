import React, {Component} from "react"
import IconButton from "material-ui/IconButton"
import MenuItem from "material-ui/MenuItem"
import IconMenu from "material-ui/IconMenu"
import MoreVertIcon from 'material-ui/svg-icons/navigation/more-vert';
import {BrowserRouter, Link, Route} from "react-router-dom";

class MainMenu extends Component {

  constructor(props) {
    super(props)
  }

  render() {
    return (
        <IconMenu
            iconButtonElement={
              <IconButton><MoreVertIcon /></IconButton>
            }
            targetOrigin={{horizontal: 'left', vertical: 'top'}}
            anchorOrigin={{horizontal: 'left', vertical: 'top'}}
        >
          <MenuItem><Link to={'/count'}>Count</Link></MenuItem>
          <MenuItem><Link to={'/players'}>Players</Link></MenuItem>
          <MenuItem primaryText="Sign out"/>
        </IconMenu>
    )

  }


}

export default MainMenu;