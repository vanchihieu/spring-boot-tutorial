package vn.java.demorestfulapi.util;

public interface  AppConst {
    String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)"; // firstName:John, age>20, age<20
    String SEARCH_SPEC_OPERATOR = "(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"; // field, operator, value, startWith, endWith
    String SORT_BY = "(\\w+?)(:)(.*)"; // firstName:asc, age:desc
}
