import React from "react";
import {render} from "react-dom";
import WhiffWhatUI from "./components/WhiffWhatUI";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import {BrowserRouter, HashRouter, Link, Route} from "react-router-dom";
import css from '../css/main.css';



const colors = require('material-ui/styles/colors');

const baseTheme = {
  palette: {
    textColor: colors.green300,
    primary1Color: colors.green500,
    accent1Color: colors.green100,
    accent2Color: colors.green200,
    accent3Color: colors.green300,

  }
};

class App extends React.Component {

  render() {
    return (
        <HashRouter>
        <MuiThemeProvider muiTheme={getMuiTheme(baseTheme)}>
          <WhiffWhatUI />
        </MuiThemeProvider>
        </HashRouter>
    );
  }
}

render(<App/>, document.getElementById('app'));