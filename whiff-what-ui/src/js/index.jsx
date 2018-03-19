import React from "react";
import {render} from "react-dom";
import WhiffWhatUI from "./components/WhiffWhatUI";
import MuiThemeProvider from "material-ui/styles/MuiThemeProvider";
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import {HashRouter} from "react-router-dom";
import { createStore } from 'redux'
import {whiffWhat} from './state/reducers/createTournamentFlowReducer'
import Provider from 'react-redux'


const colors = require('material-ui/styles/colors');

const baseTheme = {
    palette: {
        pickerHeaderColor: colors.green500,
        textColor: colors.darkslategray,
        primary1Color: colors.green500,
        primary2Color: colors.green500,
        accent1Color: colors.brown500,
        accent2Color: colors.green200,
        accent3Color: colors.green300,

    }
};

const store = createStore(whiffWhat);


class App extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
                <HashRouter>
                    <MuiThemeProvider muiTheme={getMuiTheme(baseTheme)}>
                        <WhiffWhatUI/>
                    </MuiThemeProvider>
                </HashRouter>
        );
    }
}

render(<App/>, document.getElementById('app'));