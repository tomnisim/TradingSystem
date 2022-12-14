import * as React from 'react';
import { DataGrid } from '@mui/x-data-grid';
import { ProductApi } from '../API/ProductApi';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import { StoreApi } from '../API/StoreApi';
import { Component } from 'react';
import IconButton from '@mui/material/IconButton'
import DeleteIcon from '@mui/icons-material/Delete';
import { Utils } from '../ServiceObjects/Utils';
import { CATCH } from '../API/ApiPaths';


export default class StoreManagmentProductsTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      store_id: this.props.store_id,
      products: [],
      snackbar: null,
      items: [],
      selected_item: [],
      selected_row: [],
      edited: null
    };
    this.productApi = new ProductApi();
    this.storeApi = new StoreApi();
    this.columns = [
      { field: 'id', headerName: 'ID', width: 80 },
      { field: 'name', headerName: 'name', width: 150, editable: true },
      { field: 'category', headerName: 'category', width: 150, editable: true },
      { field: 'price', headerName: 'price', type: 'double', width: 150, editable: true },
      { field: 'quantity', headerName: 'quantity', type: 'number', width: 150, editable: false },
      { field: 'store', headerName: 'store', width: 150, hide: true },
      { field: 'key_words', headerName: 'key words', width: 150 },
      {
        field: "action",
        headerName: "Action",
        width: 250,
        // Important: passing id from customers state so I can delete or edit each user
        renderCell: (id) => (
          <>
            <IconButton aria-label="delete" onClick={() => this.remove_products(id)}>
              <DeleteIcon />
            </IconButton>
          </>)
      },];

  }
  async componentDidMount() {

    let products = await this.storeApi.get_products_by_store_id(this.state.store_id);
    console.log(products);
    let products_list = []
    products.value.map(p => products_list.push(
      { id: p.product_id, name: p.name, category: p.category, price: p.price, quantity: p.quantity, store: p.store_id, key_words: p.key_words }
    ))
    this.setState({ items: products_list, products: products_list, selected: undefined });
  }

  setSnackbar = (val) => { this.setState({ snackbar: val }); };
  setItems = (val) => { this.setState({ items: val }) }
  set_selected = (val) => { this.setState({ selected_item: val }) }
  set_row_selected = (val) => { this.setState({ selected_row: val }) }
  set_edited = (val) => { this.setState({ edited: val }) }

  edit_function = async (oldRow, newRow) => {
    if (oldRow.quantity !== newRow.quantity) {
      return await this.productApi.edit_product_quantity(newRow.id, newRow.store, newRow.quantity);
    }
    if (oldRow.price !== newRow.price) {
      return await this.productApi.edit_product_price(newRow.id, newRow.store, newRow.price);
    }
    else if (oldRow.name !== newRow.name) {
      return await this.productApi.edit_product_name(newRow.id, newRow.store, newRow.name);
    }
    else if (oldRow.category !== newRow.category) {
      return await this.productApi.edit_product_category(newRow.id, newRow.store, newRow.category);
    }

  }

  handleCloseSnackbar = () => this.setSnackbar(null);

  processRowUpdate =
    async (newRow, oldRow) => {
      if (newRow.quantity < 1) {
        this.setSnackbar({ children: 'item quantity must be above 0', severity: 'error' });
        return oldRow;
      }
      if (Utils.check_holder(newRow.category) == 0) {
        console.log("empty here");
        this.setSnackbar({ children: "Illegal Category", severity: "error", });
        return oldRow;
      }
      if (Utils.check_holder(newRow.name) == 0) {
        this.setSnackbar({ children: "Illegal name", severity: "error", });
        return oldRow;
      }
      if (Utils.check_all_digits(newRow.price) == 0 || newRow.price == 0) {
        this.setSnackbar({ children: "Illegal price", severity: "error" });
        return oldRow;
      }

      let response = await this.edit_function(oldRow, newRow)
      console.log(response.was_exception)
      console.log(response)

      if (response.was_exception) {
        this.setState({ snackbar: { children: response.message, severity: 'error' } });
        return oldRow;
      }
      let new_list = this.state.items.filter(p => p.id !== newRow.id)
      new_list.push(newRow)
      new_list.sort((a, b) => (a.id - b.id));
      this.setState({ items: new_list })
      // if (response.message == "The system is not available right now, come back later")
      this.setSnackbar({ children: response.message, severity: 'success' });
      return newRow;
    }


  remove_products = async (id) => {
    let selected = this.state.items.find(i => id.id === i.id)
    let response = await this.storeApi.delete_product_from_store(selected.id, this.state.store_id);
    if (response.was_exception)
      this.setSnackbar({ children: response.message, severity: 'error' });
    else {
      this.setSnackbar({ children: 'product removed successfully', severity: 'success' });
      let new_items = this.state.items.filter(i => selected.id !== i.id)
      this.setState({ items: new_items, selected_item: [] })
    }
  }

  handleProcessRowUpdateError = (error) => {
    this.setState({ snackbar: { children: error.message, severity: "error" } });
  };

  render() {
    return (
      <main>
        <div style={{ height: 400, width: '100%' }}>
          <h1 style={{ color: "white" }}>---------------------------------------------------------</h1>
          <DataGrid rows={this.state.items} columns={this.columns}
            editMode='row'
            //   checkboxSelection
            onSelectionModelChange={(newSelectionModel) => {
              this.set_row_selected(newSelectionModel);
            }}
            processRowUpdate={this.processRowUpdate}
            onProcessRowUpdateError={this.handleProcessRowUpdateError}
            experimentalFeatures={{ newEditingApi: true }}
          >
          </DataGrid>
          {!!this.state.snackbar && (
            <Snackbar
              open
              anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
              onClose={this.handleCloseSnackbar}
              autoHideDuration={6000}
            >
              <Alert {...this.state.snackbar} onClose={this.handleCloseSnackbar} />
            </Snackbar>
          )}
        </div>

      </main>
    );
  }
}
