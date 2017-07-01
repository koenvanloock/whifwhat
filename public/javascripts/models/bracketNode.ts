module TournamentManagement{
    export interface BracketNode{
        value: Object  //should add match
        left?: BracketNode
        right?: BracketNode
    }
}