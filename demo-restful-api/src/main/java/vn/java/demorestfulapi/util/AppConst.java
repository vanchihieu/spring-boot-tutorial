package vn.java.demorestfulapi.util;

public class AppConst {
    String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)"; // firstName:John, age>20, age<20
    String SEARCH_SPEC_OPERATOR = "(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"; // firstName:John, firstName~John, firstName!John
    String SORT_BY = "(\\w+?)(:)(.*)"; // firstName:asc, age:desc
}
