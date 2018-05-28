var myId;
var opId;



$(document).ready(function() {

    var app = new Vue({
        el: "#app-content",
        data: {
            url: '/api/games',
            allData: "",
            gamesData: "",
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
                        app.gamesData = app.allData["games"];
                        console.log(app.gamesData);
                        app.findPlayers();
                        app.checkIfLogIn();
                    }
                })
            },

            findPlayers: function() {
                for (var index in this.gamesData) {
                    var gamePlayer = this.gamesData[index].gamePlayers;
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

                for (var index in this.gamesData) {
                    var gamePlayer = this.gamesData[index].gamePlayers;
                    var scores = this.gamesData[index].scores;
                    for (var each in gamePlayer) {
                        var playerUsername = gamePlayer[each].player.username;
                        if (playerUsername == thisPlayer) {
                            var playerId = gamePlayer[each].player.id;
                            if (this.gamesData[index].hasOwnProperty("scores")) {
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
            },

            checkIfLogIn: function () {
                if (this.allData["player"] != null) {
                    $("#login-btn").hide();
                    $("#signin-btn").hide();
                    $("#logout-btn").show();
                    $("#login-error-alert").hide();
                } else {
                    $("#login-btn").show();
                    $("#signin-btn").show();
                    $("#logout-btn").hide();
                }
            },

            login: function (a) {
                $.post("/api/login",
                    { userName: $("#" + a + "-email-input").val() ,
                        password: $("#" + a +"-password-input").val()
                    })
                    .done(function () {
                        $("#inner-" + a + "-btn").hide();
                        $(".alert").hide();
                        $("#login-btn").hide();
                        $("#signin-btn").hide();
                        $("#logout-btn").show();
                        $(".modal-body").hide();
                        $("#" + a + "-success-alert").show();
                    })
                    .fail(function () {
                        $("#login-password-input").select();
                        $(".alert").hide();
                        $("#login-error-alert").show();
                    });
            },

            logout: function() {
                $.post("/api/logout") //typo, on the plan of attack it misses " on the /logout
                    .done(function () {
                        $("#logout-btn").hide();
                        $(".alert").hide();
                        $("#logout-success-alert").show().fadeOut(2000, function() {
                            $("#login-btn").show();
                            $("#signin-btn").show();
                        });
                        $(".inner-btn").show();
                        $(".modal-body").show();
                        $("#signin-conf-password-input").removeClass("wrong_conf_pass");
                    });
            },

            signinverification: function() {
                var email = $("#signin-email-input").val();
                var password = $("#signin-password-input").val();

                if (email.length !== 0 && app.validateEmail(email)) {
                    if (password.length >= 4 && password.length <= 16) {
                        if (password === $("#signin-conf-password-input").val()) {
                            this.signin();
                        } else {
                            $("#signin-password-input").select();
                            $(".alert").hide();
                            $("#signin-conf-password-input").addClass("wrong_conf_pass");
                        }
                    } else {
                        $("#signin-password-input").select();
                        $(".alert").hide();
                        $("#signin-invalid-password-alert").show();
                    }

                } else {

                    $(".alert").hide();
                    $("#signin-invalid-error-alert").show();
                    $("#signin-email-input").select();

                }
            },

            validateEmail: function(email) {
                var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                return re.test(String(email).toLowerCase());
            },

            signin: function() {
                $.post("/api/players",
                    { userName: $("#signin-email-input").val() ,
                        password: $("#signin-password-input").val()
                    })
                    .done(function () {
                        $(".alert").hide();
                        app.login("signin");
                    })
                    .fail(function () {
                        $(".alert").hide();

                        $("#signin-already-error-alert").show();
                    });
            }
        }
    })
});
