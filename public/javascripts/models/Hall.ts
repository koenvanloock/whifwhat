module TournamentManagement{
    export interface Hall{
        id: string;
        name: string;
        rows: number;
        tablesPerRow: number;

        tables: Array<Table>;


    }
}