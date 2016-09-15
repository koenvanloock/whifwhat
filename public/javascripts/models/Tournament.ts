module TournamentManagement{
   export interface Tournament{
       id: string;
       tournamentName: string;
       tournamentDate: Date;
       hasMultipleSeries: boolean;
       maximumNumberOfSeriesEntries: number;
       showClub: boolean;
       series?: Array<Series>


   }
}