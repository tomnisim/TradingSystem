export class Statistics {
    // public String init_system_time;
    // private long login_per_minutes;
    // private long logout_per_minutes;
    // private long connect_per_minutes;
    // private long register_per_minutes;
    // private long buy_cart_per_minutes;
    constructor(data) {
      this.init_system_time = data.init_system_time;
      this.login_per_minutes = data.login_per_minutes;
      this.logout_per_minutes = data.logout_per_minutes;
      this.connect_per_minutes = data.connect_per_minutes;
      this.register_per_minutes = data.register_per_minutes;
      this.buy_cart_per_minutes = data.buy_cart_per_minutes;
      this.num_of_users = data.num_of_users;
      this.num_of_onlines = data.num_of_onlines;
      this.num_of_guests = data.num_of_guests;
      this.num_of_non_managers_and_owners = data.num_of_non_managers_and_owners;
      this.managers_but_not_owners_or_founders = data.managers_but_not_owners_or_founders;
      this.owners_or_founders = data.owners_or_founders;
    }
  
    static create(
      init_system_time,
      login_per_minutes,
      logout_per_minutes,
      connect_per_minutes,
      register_per_minutes,
      buy_cart_per_minutes,
      num_of_users,
      num_of_onlines,
      num_of_guests,
      num_of_non_managers_and_owners,
      managers_but_not_owners_or_founders,
      owners_or_founders
    ) {
      return new Statistics({
        init_system_time: init_system_time,
        login_per_minutes: login_per_minutes,
        logout_per_minutes: logout_per_minutes,
        connect_per_minutes: connect_per_minutes,
        register_per_minutes: register_per_minutes,
        buy_cart_per_minutes: buy_cart_per_minutes,
        num_of_users: num_of_users,
        num_of_onlines: num_of_onlines,
        num_of_guests: num_of_guests,
        num_of_non_managers_and_owners: num_of_non_managers_and_owners,
        managers_but_not_owners_or_founders: managers_but_not_owners_or_founders,
        owners_or_founders: owners_or_founders,
      });
    }
  }
  