package vn.java.demorestfulapi.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.java.demorestfulapi.model.User;

import java.util.ArrayList;
import java.util.List;

import static vn.java.demorestfulapi.repository.specification.SearchOperation.*;

public final class UserSpecificationsBuilder {

    public final List<SpecSearchCriteria> params;

    public UserSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    // API
    public UserSpecificationsBuilder with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public UserSpecificationsBuilder with(final String orPredicate, final String key, final String operation, final Object value, final String prefix, final String suffix) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == EQUALITY) { // the operation may be complex operation
                final boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = ENDS_WITH;
                } else if (endWithAsterisk) {
                    searchOperation = STARTS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(orPredicate, key, searchOperation, value));
        }
        return this;
    }

    /**
     * Giả sử bạn có danh sách params như sau:
         * List<SpecSearchCriteria> params = List.of(
         *     new SpecSearchCriteria(null, "name", SearchOperation.EQUALITY, "John"),
         *     new SpecSearchCriteria("'", "age", SearchOperation.GREATER_THAN, 30),
         *     new SpecSearchCriteria(null, "city", SearchOperation.EQUALITY, "New York")
         * );
     * Phương thức build sẽ hoạt động như sau:
     *
     *  1. Khởi tạo result với tiêu chí đầu tiên (name = 'John').
     *  2. Lặp qua tiêu chí thứ hai (age > 30) và thứ ba (city = 'New York'):
     *    - Tiêu chí thứ hai có orPredicate = true, nên kết hợp OR với result.
     *    - Tiêu chí thứ ba có orPredicate = false, nên kết hợp AND với result.
     *  Kết quả cuối cùng là một Specification đại diện cho truy vấn:
     * @return name = 'John' OR age > 30 AND city = 'New York'
     */
    public Specification<User> build() {
        if (params.isEmpty())
            return null;

        Specification<User> result = new UserSpecification(params.get(0)); // sẽ tạo ra một Specification từ SpecSearchCriteria đầu tiên trong danh sách params

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new UserSpecification(params.get(i)))
                    : Specification.where(result).and(new UserSpecification(params.get(i)));
        }

        return result;
    }

    public UserSpecificationsBuilder with(UserSpecification spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public UserSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}
