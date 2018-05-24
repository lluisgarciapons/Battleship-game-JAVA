var myId;
var opId;

$(document).ready(function() {
    var app = new Vue({
        el: "#app-content",
        data: {
            url: '/api/games',
            allData: "",
            players: []
        },

        created: function() {
            console.log("it is created.");
            this.getData();
        },

        methods: {
            getData: function() {
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: this.url,

                    success: function (json) {
                        app.allData = json;
                        console.log(app.allData);
                        app.findPlayers();
                    }
                })
            },

            findPlayers: function() {
                for (var index in this.allData) {
                    var gamePlayer = this.allData[index].gamePlayers;
                    for (var each in gamePlayer) {
                        var username = gamePlayer[each].player.username;
                        if (!this.players.includes(username)) {
                            this.players.push(username);
                        }
                    }
                }
                console.log(this.players);
            },

            findScores: function(thisPlayer, what) {
                var total = 0.0;
                var wins = 0;
                var loses = 0;
                var ties = 0;

                for (var index in this.allData) {
                    var gamePlayer = this.allData[index].gamePlayers;
                    var scores = this.allData[index].scores;
                    for (var each in gamePlayer) {
                        var playerUsername = gamePlayer[each].player.username;
                        if (playerUsername == thisPlayer) {
                            var playerId = gamePlayer[each].player.id;
                            if (this.allData[index].hasOwnProperty("scores")) {
                                for (score in scores) {
                                    if (scores[score].playerId == playerId) {
                                        total += scores[score].score;
                                        if (scores[score].score == 1) {
                                            wins++;
                                        }else if (scores[score].score == 0.5) {
                                            ties++;
                                        }else {
                                            loses++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                switch (what){
                    case "total":
                        return total.toFixed(1);
                        break;
                    case "wins":
                        return wins;
                        break;
                    case "loses":
                        return loses;
                        break;
                    case "ties":
                        return ties;
                        break;
                }
            },

            getPlayerTwo: function(data) {
                if(data.gamePlayers.length == 2) {
                    return data.gamePlayers[1].player.username;
                }else {
                    return "[WAITING FOR AN OPPONENT]";
                }
            },

            getDate: function(data) {
                return new Date(data.created).toLocaleString();
            }
        }
    })
});
