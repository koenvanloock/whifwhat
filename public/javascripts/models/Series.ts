module TournamentManagement{
    export interface Series{
        id: string,
        seriesName: string,
        seriesColor: string,
        setTargetScore: number,
        numberOfSetsToWin: number,
        playingWithHandicaps: boolean,
        extraHandicapForRecs: number,
        showReferees: boolean,
        tournamentId: string,
        currentRoundNr: number,

        seriesPlayers?: Array<Player>;
        rounds?: Array<SeriesRound>;
    }
}