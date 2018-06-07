
$(document).ready(function() {

    var app = new Vue({
        el: "#app-content",
        data: {
            url: '/api/games',
            allData: "",
            gamesData: "",
            players: [],
            myId: ""
        },

        created: function() {
            $("#all").show();
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
                        if (app.allData["player"] != null) {
                            app.myId = app.allData["player"].id;
                        } else {
                            app.myId = null;
                        }
                        app.findPlayers();
                        app.checkIfLogIn();
                    }
                });
            },

            updateData: function () {
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: this.url,

                    success: function (json) {
                        app.allData = json;
                        app.gamesData = app.allData["games"];
                        app.checkIfLogIn();
                        if (app.allData["player"] != null) {
                            app.myId = app.allData["player"].id;
                        } else {
                            app.myId = null;
                        }
                    }
                });
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
                    return "<span class='only_one_player'>WAITING...</span>";
                }
            },

            getDate: function(data) {
                return new Date(data.created).toLocaleString();
            },

            checkIfLogIn: function () {
                if (this.allData["player"] != null) {
                    this.sayHiToUser();
                    $("#login-btn").hide();
                    $("#signup-btn").hide();
                    // $("#after-login").show();
                    $("#login-error-alert").hide();
                } else {
                    $("#login-btn").show();
                    $("#signup-btn").show();
                    $("#after-login").hide();
                }
            },

            sayHiToUser: function() {
                var username = this.allData["player"].username;
                $("#hi-user")[0].innerHTML = username;
                $("#after-login").show();
            },

            login: function (source) {
                $.post("/api/login",
                    { email: $("#" + source + "-email-input").val() ,
                        password: $("#" + source +"-password-input").val()
                    })
                    .done(function () {
                        app.updateData();
                        $("#inner-" + source + "-btn").hide();
                        $(".alert").hide();
                        $("#login-btn").hide();
                        $("#signup-btn").hide();
                        // $("#after-login").show();
                        $(".modal-body").hide();
                        $("#" + source + "-success-alert").show();
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
                        app.updateData();
                        $("#after-login").hide();
                        $(".alert").hide();
                        $("#login-email-input").val($("#signup-email-input").val());
                        $("#login-password-input").val("");
                        $("#signup-password-input").val("");
                        $("#signup-conf-password-input").val("");
                        $("#signup-username-input").val("");
                        $("#signup-email-input").val("");
                        $("#logout-success-alert").show().fadeOut(2000);
                        $("#login-btn").show();
                        $("#signup-btn").show();
                        $(".inner-btn").show();
                        $(".modal-body").show();
                        $("#signup-conf-password-input").removeClass("wrong_conf_pass");
                    });
            },

            signupverification: function() {
                var username = $("#signup-username-input").val();
                var email = $("#signup-email-input").val();
                var password = $("#signup-password-input").val();

                if (username.length !== 0 && username.length <= 28) {
                    if(email.length !== 0 && app.validateEmail(email)) {
                        if (password.length >= 4 && password.length <= 16) {
                            if (password === $("#signup-conf-password-input").val()) {
                                this.signup();
                            } else {
                                $("#signup-password-input").select();
                                $(".alert").hide();
                                $("#signup-conf-password-input").addClass("wrong_conf_pass");
                                $("#signup-conf-password-input").val("");
                            }
                        } else {
                            $("#signup-password-input").select();
                            $(".alert").hide();
                            $("#signup-invalid-password-alert").show();
                        }
                    } else {
                        $(".alert").hide();
                        $("#signup-invalid-email-alert").show();
                        $("#signup-email-input").select();
                    }

                } else {
                    $(".alert").hide();
                    $("#signup-invalid-username-alert").show();
                    $("#signup-username-input").select();
                }
            },

            signup: function() {
                $.post("/api/players",
                    { userName: $("#signup-username-input").val(),
                        email: $("#signup-email-input").val(),
                        password: $("#signup-password-input").val()
                    })
                    .done(function (json) {
                        console.log(json);
                        $(".alert").hide();
                        $("#signup-success-alert").text("Welcome aboard " + json.userName + "!");
                        $("#login-email-input").val($("#signup-email-input").val());
                        app.login("signup");
                    })
                    .fail(function (json) {
                        console.log(json);
                        $(".alert").hide();
                        $("#signup-error-alert").text(json.responseJSON.error).show();
                    });
            },

            validateEmail: function(email) {
                var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                return re.test(String(email).toLowerCase());
            },

            createButton: function(game) {
                if (this.allData["player"] != null) {
                    if (game.gamePlayers.length == 2 || game.gamePlayers[0].player.id == this.myId) {
                        for (var i = 0; i < game.gamePlayers.length; i++) {
                            var playerId = game.gamePlayers[i].player.id;
                            var output = "";
                            console.log("my Id: " + this.myId);
                            console.log("player " + (i + 1) + " Id from game " + game.id + ": " + playerId);
                            if (playerId == this.myId) {
                                output = "<button onclick='redirect(" + game.gamePlayers[i].id + ")' class='enter_btn' data-gpid ='"+ game.gamePlayers[i].id +"' data-id='enter'>Enter Game</button>";
                                break;
                            } else {
                                output =  "-";
                            }
                        }
                        return output;
                    } else {
                        var gameId = game.id;
                        return "<button onclick='joinGame(" + gameId + ")' class='join_btn' data-id='join' data-game='" + gameId + "'>Join Game</button>";
                    }
                } else {
                    return "-";
                }
            },

            newGame: function() {
                $.post("/api/games")
                    .done(function(json) {
                        // location.reload();
                        redirect(json.gpid);
                    })
                    .fail(function(json) {
                        $("#unauthorized-error-alert").text(json.responseJSON.error);
                        $("#unauthorized-modal").modal("show");
                    })
            },
        }
    });
});

function redirect(gpid) {
    console.log("does it work?");
    console.log(this);

    var url  = "http://localhost:8080/web/game.html?gp=" + gpid;
    console.log(url);
    window.location = url;
}

function joinGame(gameId) {
    console.log(gameId);
    $.post("/api/game/" + gameId + "/players")
        .done(function(json) {
            console.log(json);
            var gpId = json.gpid;
            redirect(gpId);
        })
        .fail(function(json) {
            console.log(json);
            $("#unauthorized-error-alert").text(json.responseJSON.error);
            $("#unauthorized-modal").modal("show");
        })
}
