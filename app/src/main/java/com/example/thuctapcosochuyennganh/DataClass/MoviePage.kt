package com.example.thuctapcosochuyennganh.DataClass

data class MoviePage(
    val movieList: List<Movie>,
    val pageNumber: Int,         // Chỉ số của trang hiện tại
    val pageSize: Int,           // Kích thước của 1 trang
    val totalElements: Long,     // Tổng số phim
    val totalPages: Int,         // Tổng số trang
    val lastPage: Boolean,       // Có phải là trang cuối cùng không
    val firstPage: Boolean       // Có phải là trang đầu tiên không
)
