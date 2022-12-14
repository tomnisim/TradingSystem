import * as React from "react";
import { DataGrid, GridActionsCellItem } from "@mui/x-data-grid";
import Button from "@mui/material/Button";
import { ProductApi } from "../API/ProductApi";
import { Row } from "react-grid-system";
import Grid from "@mui/material/Grid";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import { StoreApi } from "../API/StoreApi";
import { Component } from "react";
import { UserApi } from "../API/UserApi";
import { CartApi } from "../API/CartApi";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import { Link } from "react-router-dom";

export default class ShoppingCart extends Component {
  constructor(props) {
    super(props);
    this.state = {
      price: 0,
      products: [],
      snackbar: null,
      items: [],
      selected_item: [],
      selected_row: [],
      edited: null,
    };
    this.productApi = new ProductApi();
    this.storeApi = new StoreApi();
    this.userApi = new UserApi();
    this.cartApi = new CartApi();
    this.columns = [
      { field: "id", headerName: "ID", width: 70 },
      { field: "name", headerName: "name", width: 130 },
      { field: "category", headerName: "category", width: 130 },
      { field: "price", headerName: "price", type: "double", width: 90 },
      {
        field: "quantity",
        headerName: "quantity",
        type: "number",
        width: 90,
        editable: true,
      },
      { field: "store", headerName: "store", width: 90, hide: true },
      { field: "key_words", headerName: "key words", width: 0, hide: true },
      {
        field: "action",
        headerName: "Remove",
        width: 250,
        // Important: passing id from customers state so I can delete or edit each user
        renderCell: (id) => (
          <>
            <IconButton
              aria-label="delete"
              onClick={() => this.remove_products(id)}
            >
              <DeleteIcon />
            </IconButton>
          </>
        ),
      },
    ];
  }
  async componentDidMount() {
    let cart = await this.cartApi.view_user_cart();
    console.log(cart);
    let products_list = [];
    this.setState({ price: cart.value.price });
    cart.value.products.map((p) =>
      products_list.push({
        id: p.product_id,
        name: p.name,
        category: p.category,
        price: p.price,
        quantity: p.quantity,
        store: p.store_id,
        key_words: p.key_words,
      })
    );
    this.setState({
      items: products_list,
      products: products_list,
      selected: undefined,
    });
  }

  setSnackbar = (val) => {
    this.setState({ snackbar: val });
  };
  setItems = (val) => {
    this.setState({ items: val });
  };
  set_selected = (val) => {
    this.setState({ selected_item: val });
  };
  set_row_selected = (val) => {
    this.setState({ selected_row: val });
  };
  set_edited = (val) => {
    this.setState({ edited: val });
  };




  handleCloseSnackbar = () => this.setSnackbar(null);

  edit_function = async (oldRow, newRow) => {
    if (oldRow.quantity !== newRow.quantity)
      return await this.productApi.edit_product_quantity_in_cart(
        newRow.store,
        newRow.id,
        newRow.quantity
      );

  };

  remove_products = async (id) => {
    let selected = this.state.items.find((i) => id.id === i.id);
    let response = await this.productApi.remove_product_from_cart(
      selected.store,
      selected.id
    );
    if (response.was_exception)
      this.setSnackbar({ children: response.message, severity: "error" });

    else {
      this.setSnackbar({
        children: "product removed successfully",
        severity: "success",
      });
      let new_items = this.state.items.filter((i) => selected.id !== i.id);
      this.setState({ items: new_items, selected_item: [] });
      window.location.reload();
    }
  };

  processRowUpdate = async (newRow, oldRow) => {
    if (newRow.quantity < 1) {
      this.setSnackbar({
        children: "item quantity must be above 0",
        severity: "error",
      });
      return oldRow;
    }
    let response = await this.edit_function(oldRow, newRow);
    if (response.was_exception) {
      this.setSnackbar({ children: response.message, severity: "error" });
      return oldRow;
    }
    if (response.message == "The system is not available right now, come back later")
      this.setSnackbar({ children: response.message, severity: "success" });
    let new_list = this.state.items.filter((p) => p.id !== newRow.id);
    new_list.push(newRow);
    new_list.sort((a, b) => a.id - b.id);
    this.setState({ items: new_list });
    return newRow;
  };

  handleProcessRowUpdateError = (error) => {
    this.setState({ snackbar: { children: error.message, severity: "error" } });
  };

  render() {
    return (
      <main>
        <div style={{ horizontal: "center", height: 400, width: "100%" }}>
          <Grid
            container
            direction="column"
            justifyContent="center"
            alignItems="center"
          >
            <Row>
              <h1 horizontal="center">Shopping cart items</h1>
            </Row>
            <Row>
              <h6>
                You can edit your products quantity by double click on quantity.
              </h6>
            </Row>
          </Grid>
          <DataGrid
            align="center"
            horizontal="center"
            rows={this.state.items}
            columns={this.columns}
            editMode="row"
            //   checkboxSelection
            onSelectionModelChange={(newSelectionModel) => {
              this.set_row_selected(newSelectionModel);
            }}

            processRowUpdate={this.processRowUpdate}
            onProcessRowUpdateError={this.handleProcessRowUpdateError}
            experimentalFeatures={{ newEditingApi: true }}

          ></DataGrid>
          <Row>
            <h1 style={{ color: "white" }}> </h1>
          </Row>
          <Grid
            container
            direction="column"
            justifyContent="center"
            alignItems="center"
          >
            <h4>cart total price {this.state.price}</h4>

            <Button width="5" variant="contained" >
              <Link to={{ pathname: `BuyCart` }} underline="hover" style={{ color: '#FFF' }} >{'Buy Cart'}</Link>
            </Button>
          </Grid>
          {!!this.state.snackbar && (
            <Snackbar
              open
              anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
              onClose={this.handleCloseSnackbar}
              autoHideDuration={6000}
            >
              <Alert
                {...this.state.snackbar}
                onClose={this.handleCloseSnackbar}
              />
            </Snackbar>
          )}
        </div>
      </main>
    );
  }
}
