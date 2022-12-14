import * as React from "react";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import Logout from "@mui/icons-material/Logout";
import { Button } from "@material-ui/core";
import { Link } from "react-router-dom";
import { ConnectApi } from '../API/ConnectApi';
import { useEffect } from 'react';
import {useState} from "react";

export default function AccountMenu({ log, state }) {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const logout = () =>{
    handleClose();
    log();
  }
  const [user, setUser] = useState(null);
  const connectApi = new ConnectApi();
  useEffect(()=>{get_online_user()}, []);
  const get_online_user = async () => {
    let response = await connectApi.get_online_user()
    if(!response.was_exception)
    {
      setUser(response.value);

    }
    else
    {

    }
    
}

  return (
    <React.Fragment>
      <Box sx={{ display: "flex", alignItems: "center", textAlign: "center" }}>
        <Tooltip title="Account settings">
          <IconButton
            onClick={handleClick}
            size="small"
            sx={{ ml: 2 }}
            aria-controls={open ? "account-menu" : undefined}
            aria-haspopup="true"
            aria-expanded={open ? "true" : undefined}
          >
            <Avatar sx={{ width: 32, height: 32 }}>M</Avatar>
          </IconButton>
        </Tooltip>
      </Box>
      <Menu
        anchorEl={anchorEl}
        id="account-menu"
        open={open}
        onClose={handleClose}
        // onClick={handleClose}
        PaperProps={{
          elevation: 0,
          sx: {
            overflow: "visible",
            filter: "drop-shadow(0px 2px 8px rgba(0,0,0,0.32))",
            mt: 1.5,
            "& .MuiAvatar-root": {
              width: 32,
              height: 32,
              ml: -0.5,
              mr: 1,
            },
            "&:before": {
              content: '""',
              display: "block",
              position: "absolute",
              top: 0,
              right: 14,
              width: 10,
              height: 10,
              bgcolor: "background.paper",
              transform: "translateY(-50%) rotate(45deg)",
              zIndex: 0,
            },
          },
        }}
        transformOrigin={{ horizontal: "right", vertical: "top" }}
        anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
      >
        <MenuItem>
          <Avatar />{" "}
          
          <Link to={{pathname:`/ShoppingCart`}} onClick={handleClose} underline="hover">{"My Cart"} </Link>
            
        </MenuItem>
        {state !== 0 ? (
          <>
            <MenuItem>
              <Avatar />
              <Link to={{pathname:`/EditProfile`}} onClick={handleClose} underline="hover">{"Edit Profile"} </Link>
                
            </MenuItem>
            <MenuItem>
              <Avatar />{" "}
              <Link to={{pathname:`/ViewUserPurchaseHistory`}} onClick={handleClose} underline="hover">{"My Purchases History"} </Link>
                
            </MenuItem>
            <MenuItem>
              <Avatar />{" "}
              <Link to={{pathname:`/UserViewQuestions`}} onClick={handleClose} underline="hover">{"My Questions"}  </Link>
                
            </MenuItem>
           
            
            <MenuItem>
              <Avatar />{" "}
              <Link to={{pathname:`/MyStores`}} onClick={handleClose} underline="hover" >{'My Stores'}</Link>
            </MenuItem>
            <Divider />
            <MenuItem>
              <ListItemIcon>
                <Logout fontSize="small" />
              </ListItemIcon>
              <Button onClick={logout}>Logout</Button>
            </MenuItem>
          </>
        ) : null}
      </Menu>
    </React.Fragment>
  );
}
