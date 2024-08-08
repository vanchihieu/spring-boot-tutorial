package vn.java.demorestfulapi.repository.specification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static vn.java.demorestfulapi.repository.specification.SearchOperation.*;

@Getter
@Setter
@NoArgsConstructor
public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate; // cờ để xác định xem predicate có phải là OR predicate hay không (mặc định là AND)


    public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(final String orPredicate, final String key, final SearchOperation operation, final Object value) {
        super();
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String key, String operation, String prefix, String value, String suffix) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == EQUALITY) { // the operation may be complex operation
                final boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX); // kiem tra xem co bat dau bang dau * hay khong
                final boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX); // kiem tra xem co ket thuc bang dau * hay khong

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = ENDS_WITH; // vd: tim kiem tat ca cac tu ket thuc bang "abc" thi se tim kiem voi key = *abc
                } else if (endWithAsterisk) {
                    searchOperation = STARTS_WITH; // vd: tim kiem tat ca cac tu bat dau bang "abc" thi se tim kiem voi key = abc*
                }
            }
        }
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }

}