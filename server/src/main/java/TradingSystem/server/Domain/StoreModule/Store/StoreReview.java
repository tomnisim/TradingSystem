package TradingSystem.server.Domain.StoreModule.Store;

import TradingSystem.server.Domain.Utils.Exception.WrongPermterException;

import java.util.HashMap;
import java.util.Map;

public class StoreReview {
    private Map<String, Integer> rating; // user_email & rating
    private int avg_rating;

    // ------------------------------ constructors ------------------------------
    public StoreReview()
    {
        avg_rating = 0;
        rating = new HashMap<>();
    }

    // ------------------------------ getters ------------------------------
    public int getAvg_rating() {
        return avg_rating;
    }

    // ------------------------------ setters ------------------------------
    public void setRating(Map<String, Integer> rating) {
        this.rating = rating;
    }

    public void setAvg_rating(int avg_rating) {
        this.avg_rating = avg_rating;
    }

    // ------------------------------ methods ------------------------------

    public void add_rating(String user_email, int rating) throws WrongPermterException {
        if (rating < 0 || rating > 5)
            throw new WrongPermterException("rating range 1-5");
        this.rating.put(user_email, rating);
        int rating_size = this.rating.size();
        if (rating_size == 1)
            this.avg_rating = rating;
        else
            this.avg_rating = (avg_rating * this.rating.size() - 1 + rating) / (this.rating.size());
    }

    public Map<String, Integer> getRating() {
        return rating;
    }

    public int getAvgRating() {
        if (rating.size() == 0)
            return 3;
        else
            return avg_rating;
    }

}