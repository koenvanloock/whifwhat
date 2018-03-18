import React from 'react'
import AppBar from 'material-ui/AppBar'
import IconButton from "material-ui/IconButton"
import MainMenu from "./MainMenu"

class Navbar extends React.Component {

  constructor(props) {
    super(props)

  }


  render() {
    return (
        <div>
          <AppBar
              title="Whiff What"
              iconElementLeft={<MainMenu />}
          />
        </div>
    )

  }

}

export default Navbar;
