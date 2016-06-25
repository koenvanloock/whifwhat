module TournamentManagement{
    export interface Series{
        seriesId: string,
        seriesName: string,
        seriesColor: string,
        setTargetScore: number,
        numberOfSetsToWin: number,
        playingWithHandicaps: boolean,
        extraHandicapForRecs: number,
        showReferees: boolean,
        tournamentId: string,

        seriesPlayers?: Array<Player>;
    }
}