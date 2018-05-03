$(document).ready(function() {
    var app = new Vue({
        el: "#app-content",
        data: {
            url: '/api/games',
            allData: "",
            gamePlayer: "",
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

            getDate: function(data) {
                var date = new Date(data.created);
                return date.toLocaleString();
            }
        }
    })
})
