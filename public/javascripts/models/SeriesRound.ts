module TournamentManagement{

    export interface SeriesRound{
        roundType: RoundType;
        roundNr: number;
        numberOfRobinGroups?: number;
        numberOfBracketRounds?: number;
        id: string;
        seriesId: string;

    }
}