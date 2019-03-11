var express =require("express");
var app=express();
var bodyParser=require("body-parser");
var mongoose=require("mongoose");
var methodOverride=require("method-override")
var passport=require("passport")
var LocalStrategy= require("passport-local")
var User=require("./models/user")
var Post=require("./models/post");
var Comment=require("./models/comment");

var commentRoutes=require("./routes/comments"),
    postRoutes=require("./routes/posts"),
    indexRoutes=require("./routes/index");

mongoose.connect("mongodb://localhost/connect+");
app.use(bodyParser.urlencoded({extended: true}));
app.set("view engine","ejs");
app.use(methodOverride("_method"));

app.use(require("express-session")({
    secret: "Peace in our time",
    resave: false,
    saveUnintialize: false
}));
app.use(passport.initialize());
app.use(passport.session());
passport.use(new LocalStrategy(User.authenticate()));
passport.serializeUser(User.serializeUser());
passport.deserializeUser(User.deserializeUser());

app.use(function(req,res,next){
    res.locals.currentUser=req.user;
    next();
});

app.use("/",indexRoutes);
app.use("/posts",postRoutes);
app.use("/posts/:id/comments",commentRoutes);

app.listen(3000,function(){
    console.log("Server started");
});

app.get("/author", function(req,res){
    res.render("author");
})