module TournamentManagement{

    export interface SeriesRound{
        roundType: RoundType;
        roundNr: number;
        numberOfRobinGroups?: number;
        numberOfBracketRounds?: number;
        id: string;
        seriesId: string;
        robinRounds? : Array<Object>; // todo add RobinGroup as type
        bracket?: BracketNode

    }
}