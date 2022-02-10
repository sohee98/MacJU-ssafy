import {BrowserRouter as Router, Switch, Route  } from "react-router-dom"
import PostList from "./routes/Post/PostList";
import PostDetail from "./routes/Post/PostDetail";
import PostCreate from "./routes/Post/PostCreate";
import BeerList from './routes/Beer/BeerList.js';
import Signup from './routes/User/Signup';
import Login from './routes/User/Login';
import Navbar from './components/Navbar.js';
import Footer from './components/Footer.js';
import BeerDetail from './routes/Beer/BeerDetail';
import Profile from "routes/User/Profile";
import Home from "routes/Home";
import PageNotFound from "components/PageNotFound";
import ProfileEdit from "routes/User/ProfileEdit";
import LoginAuth from "routes/LoginAuth";
import Search from "routes/Search"

function App() {
  return (
    <div>
      <Router>
        <Navbar />
        <Switch>
          <Route path="/home" component={Home} />
          <Route path="/post">
            <Switch>
              <Route path="/post/new" component={PostCreate} />
              <Route path="/post/:postId" component={PostDetail} />
              <Route path="/post" component={PostList} />
            </Switch>
          </Route>
          <Route path="/beer">
            <Switch>
              <Route path="/beer/:beerid" component={BeerDetail} />
              <Route path="/beer" component={BeerList} />
            </Switch>
          </Route>
          <Route path="/user">
            <Route path="/user/login" component={Login} />
            <Route path="/user/signup" component={Signup} />
          </Route>
          <Route path="/profile">
            <Switch>
              <Route path="/profile/:userid/edit" component={ProfileEdit} />
              <Route path="/profile/:userid/" component={Profile} />
            </Switch>
          </Route>
          <Route path="/oauth/login/resopnse" component={LoginAuth}/>
          <Route path="/search" component={Search} />
          <Route path="*" component={PageNotFound} />
        </Switch>
        <Footer />
      </Router>
    </div>
  );
}



export default App;
