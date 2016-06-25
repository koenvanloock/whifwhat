module TournamentManagement{
   export interface Tournament{
       tournamentId: string;
       tournamentName: string;
       tournamentDate: Date;
       hasMultipleSeries: boolean;
       maximumNumberOfSeriesEntries: number;
       showClub: boolean;
       series?: Array<Series>


   }
}