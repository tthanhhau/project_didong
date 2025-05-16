# 🛍️ Ứng dụng Bán Quần Áo - Android + Spring Boot + MySQL

Đây là một hệ thống bán hàng thời trang gồm ứng dụng Android cho người dùng và backend Spring Boot sử dụng RESTful API kết nối với cơ sở dữ liệu MySQL. Hệ thống hỗ trợ đầy đủ chức năng mua sắm, quản lý tài khoản và xử lý đơn hàng.

---

## 🧩 Kiến trúc hệ thống

Android App
   ⇄ Retrofit2 ⇄
Spring Boot API
   ⇄ MySQL
    ⇅
 Cloudinary





## 🚀 Tính năng chính

## 👤 Chức năng Người Dùng

- 🏠 **Trang chủ:** Xem sản phẩm mới, nổi bật
- 🔎 **Tìm kiếm:** Theo tên, từ khóa
- 📋 **Chi tiết sản phẩm:** Mô tả, giá, danh mục, đánh giá sản phẩm khi đã mua
- 📂 **Theo dõi danh mục:** Hiển thị sản phẩm cùng loại
- 🔐 **Tài khoản:** Đăng ký, đăng nhập, cập nhật, đổi mật khẩu
- ❓ **Quên mật khẩu:** Nhận mật khẩu mới qua Gmail
- 🛒 **Giỏ hàng:** Thêm/sửa/xoá sản phẩm (hỗ trợ vuốt để xoá)
- 🧾 **Đặt hàng:** Có thể chỉnh sửa địa chỉ nhận hàng
- 💳 **Thanh toán:**
  - COD (Khi nhận hàng)
  - Ví điện tử ZaloPay (mô phỏng)
- 🖼️ **Ảnh đại diện:** Upload ảnh lên Cloudinary, lưu vào database
- 📜 **Lịch sử mua hàng:** Xem lại đơn đã đặt

---

## 🛠️ Trang Admin

- 📦 Quản lý sản phẩm, loại sản phẩm
- 📇 Quản lý đơn hàng, khách hàng
- 📈 Thống kê doanh thu:
  - Theo hình thức thanh toán
  - Theo sản phẩm bán ra
- 🧾 Quản lý hóa đơn

---

## 🧪 Công nghệ sử dụng

| Thành phần       | Công nghệ               | Vai trò                                |
|------------------|--------------------------|-----------------------------------------|
| 📱 Ứng dụng      | Android (Java)           | Ứng dụng di động                        |
| 🌐 Backend       | Spring Boot              | Cung cấp API                            |
| 🔐 Bảo mật        | Spring Security + JWT    | Xác thực người dùng                     |
| 💾 Database      | MySQL                    | Lưu trữ dữ liệu                         |
| 🔗 Giao tiếp     | Retrofit2                | Giao tiếp Android ↔ REST API            |
| ☁️ Ảnh           | Cloudinary               | Lưu trữ hình ảnh                        |
| 📧 Gửi Email     | JavaMail API             | Gửi thông báo, xác nhận đơn hàng        |

---

## ⚙️ Hướng dẫn cài đặt

### 🔧 Yêu cầu hệ thống

| Công cụ                | Link tải                                           |
|------------------------|----------------------------------------------------|
| Java JDK 11+           | https://www.oracle.com/java/technologies/javase-jdk11-downloads.html |
| MySQL                  | https://dev.mysql.com/downloads/                  |
| Eclipse / IntelliJ     | https://www.eclipse.org / https://www.jetbrains.com/idea/download |
| Android Studio         | https://developer.android.com/studio             |

---

📥 Các bước cài đặt
🔧 API (Spring Boot)
Tải source code backend và mở bằng Eclipse hoặc IntelliJ IDEA.

Đảm bảo đã cài đặt Spring Boot và Lombok.

Cấu hình lại file application.properties để kết nối đúng tài khoản & mật khẩu MySQL.

Chạy project Spring Boot.

🗄️ Database
Mở MySQL.

Chạy file script.sql đi kèm trong source để tạo database.

📱 Mobile App (Android)
Mở source code mobile bằng Android Studio.

Cập nhật địa chỉ IP backend và đường dẫn tích hợp ZaloPay theo hướng dẫn trong README của ứng dụng mobile.
