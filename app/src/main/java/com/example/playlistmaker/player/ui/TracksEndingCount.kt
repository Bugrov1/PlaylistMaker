package com.example.playlistmaker.player.ui

class TracksEndingCount() {

    fun tracksString(numberOfTracks:Int):String {

        if(numberOfTracks%100>=11&&numberOfTracks%100<=19){
            return "$numberOfTracks треков";
        }
        else{
            if(numberOfTracks%10==1){
                return "$numberOfTracks трек";
            }
            else if (numberOfTracks%10>=2&&numberOfTracks%10<=4){
                return "$numberOfTracks трека";
            }
            else if (numberOfTracks%10>=5&&numberOfTracks%10<=9){
            return "$numberOfTracks треков";
            }else {
                return  "$numberOfTracks треков";
            }
        }
    }
}