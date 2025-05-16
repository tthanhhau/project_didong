# 🛍️ Ứng dụng Bán Quần Áo - Android + Spring Boot + MySQL

Đây là một hệ thống bán hàng thời trang gồm ứng dụng Android cho người dùng và backend Spring Boot sử dụng RESTful API kết nối với cơ sở dữ liệu MySQL. Hệ thống hỗ trợ đầy đủ chức năng mua sắm, quản lý tài khoản và xử lý đơn hàng.

---

## 🧩 Kiến trúc hệ thống


---

## 🚀 Tính năng chính

### 📱 Ứng dụng Android
- 👤 Đăng ký, đăng nhập người dùng (JWT)
- 🧥 Duyệt danh mục và sản phẩm
- 🔍 Tìm kiếm sản phẩm
- 🛒 Quản lý giỏ hàng
- 📦 Đặt hàng, theo dõi đơn
- 🎟️ Áp dụng mã giảm giá
- 👤 Chỉnh sửa thông tin cá nhân
- 🖼️ Hiển thị ảnh sản phẩm bằng Glide

### 🌐 Backend Spring Boot
- 🔐 Xác thực người dùng bằng JWT
- 🧰 CRUD: Sản phẩm, danh mục, tài khoản, đơn hàng
- 📁 Upload hình ảnh sản phẩm
- 📊 Thống kê đơn hàng
- 💬 Phản hồi API dạng JSON
👤 Chức năng Người Dùng
Trang chủ: Xem sản phẩm mới, sản phẩm bán chạy.
Tìm kiếm sản phẩm: Theo tên, từ khóa.
Xem chi tiết sản phẩm
Xem sản phẩm cùng loại (theo danh mục)
Đăng ký / Đăng nhập / Cập nhật thông tin cá nhân / Đổi mật khẩu
Quên mật khẩu: Gửi mật khẩu mới qua Gmail.
Quản lý giỏ hàng: Thêm, sửa, xoá sản phẩm (hỗ trợ vuốt để xoá).
Đặt hàng: Cho phép sửa địa chỉ nhận hàng.
Thanh toán:
Khi nhận hàng (COD)
Qua ví điện tử ZaloPay
Thông tin cá nhân: Cho phép upload hình ảnh lên Cloudinary và lưu link ảnh vào database.
Lịch sử mua hàng: Xem tất cả đơn hàng theo hình thức thanh toán.

🛠️ Trang Admin
Quản lý cơ sở dữ liệu: Tổng quan sản phẩm, loại sản phẩm, đơn hàng, khách hàng.
Quản lý đơn hàng
Quản lý sản phẩm: Thêm, sửa, xóa sản phẩm.
Thống kê doanh thu bán hàng:
Theo hình thức thanh toán
Theo số lượng sản phẩm và khách hàng
Cho phép gửi email cho khách khi đặt hàng
Quản lý hóa đơn

## 🛠️ Công nghệ sử dụng

| Layer         | Công nghệ            | Mô tả                             |
|---------------|----------------------|-----------------------------------|
| 📱 Giao diện  | Android (Java)       | Ứng dụng di động cho người dùng   |
| 🌐 API        | Spring Boot          | Backend xử lý và cung cấp dữ liệu |
| 🔒 Bảo mật     | Spring Security + JWT| Xác thực và phân quyền API        |
| 💾 Cơ sở dữ liệu | MySQL              | Lưu trữ dữ liệu hệ thống          |
| 🔗 Kết nối    | Retrofit2             | Kết nối Android ↔ API             |
| 🖼️ Hình ảnh   | Cloudinary            | Load ảnh trong Android            |

---

## ⚙️ Hướng dẫn cài đặt

🔧 Công cụ cần thiết:
Công cụ	Tải về
Java JDK 11+	https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
MySQL	https://dev.mysql.com/downloads/
Eclipse IDE hoặc IntelliJ IDEA	https://www.eclipse.org/downloads/ / https://www.jetbrains.com/idea/download
Android Studio	https://developer.android.com/studio
### 🖥️ Backend (Spring Boot)
1. Tạo database `shopdb` trong MySQL
2. Cấu hình `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/shopdb
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
