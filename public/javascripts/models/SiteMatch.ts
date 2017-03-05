module TournamentManagement{
    export interface SiteMatch{
        id: string
        playerA?: Player
        playerB?: Player
        handicap: number
        isHandicapForB: boolean
        games: Array<Game>
    }
}