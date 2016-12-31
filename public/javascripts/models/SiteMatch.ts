module TournamentManagement{
    export interface SiteMatch{
        id: string
        playerA?: Player
        playerB?: Player
        handicap: number
        isHandicapForA: boolean
        games: Array<Game>
    }
}