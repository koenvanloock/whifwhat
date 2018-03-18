
export const ADD_TOURNAMENT = 'add_tournament';
export const ADD_SERIES = 'add_series';
export const UPDATE_SERIES = 'edit_series';
export const DELETE_SERIES = 'delete_series';

export function addTournament(tournament) {
    return {type: ADD_TOURNAMENT, tournament: tournament}
}

export function addSeries(series) {
    return {type: ADD_SERIES, series: series}
}

export function updateSeries(series) {
    return {type: UPDATE_SERIES, series: series}
}

export function deleteSeries(seriesId) {
    return {type: DELETE_SERIES, seriesId: seriesId}
}