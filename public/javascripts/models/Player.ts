module TournamentManagement{
    export interface Player{
        id: string;
        firstname: string;
        lastname: string;
        rank: Rank;
        imagepath?: string;


    }
}