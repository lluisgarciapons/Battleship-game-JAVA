var myId;
var opId;

$(document).ready(function() {
    var app = new Vue({
        el: "#app-content",
        data: {
            url: '/api/games',
            allData: "",
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
                    }
                })
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
