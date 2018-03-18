import {ADD_TOURNAMENT} from "../actions/creationTournamentFlowActions";

const initialState = {
    currentTournament: {}
};

function whiffWhat(state, action) {
    if (typeof state === 'undefined') {
        return initialState
    }


    switch (action.type) {
        case ADD_TOURNAMENT: return Object.assign({}, state, {currentTournament: action.tournament});
        default: return state;
    }
}