module TournamentManagement{
   export class Tournament{
       constructor(
       private tournamentName: string,
       private tournamentDate: Date,
       private hasMultipleSeries: boolean,
       private maximumNumberOfSeriesEntries: number,
       private showClub: boolean){}


   }
}