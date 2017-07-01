module TournamentManagement{
    export interface Hall{
        id: string;
        hallName: string;
        rows: number;
        tablesPerRow: number;

        tables: Array<Table>;


    }
}