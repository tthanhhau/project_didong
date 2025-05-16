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
- 👤 Cập nhật thông tin cá nhân
- 🖼️ Hiển thị ảnh sản phẩm bằng Glide
- 🕓 Lịch sử mua hàng

### 🌐 Backend Spring Boot
- 🔐 Xác thực người dùng bằng JWT
- 🧰 CRUD: Sản phẩm, danh mục, tài khoản, đơn hàng
- 📁 Upload ảnh sản phẩm lên Cloudinary
- 📊 Thống kê đơn hàng, doanh thu
- 💬 Trả về dữ liệu dạng JSON
- 📧 Gửi email xác nhận / lấy lại mật khẩu

---

## 👤 Chức năng Người Dùng

- 🏠 **Trang chủ:** Xem sản phẩm mới, nổi bật
- 🔎 **Tìm kiếm:** Theo tên, từ khóa
- 📋 **Chi tiết sản phẩm:** Mô tả, giá, danh mục
- 📂 **Theo dõi danh mục:** Hiển thị sản phẩm cùng loại
- 🔐 **Tài khoản:** Đăng ký, đăng nhập, cập nhật, đổi mật khẩu
- ❓ **Quên mật khẩu:** Nhận mật khẩu mới qua Gmail
- 🛒 **Giỏ hàng:** Thêm/sửa/xoá sản phẩm (hỗ trợ vuốt để xoá)
- 🧾 **Đặt hàng:** Có thể chỉnh sửa địa chỉ nhận hàng
- 💳 **Thanh toán:**
  - COD (Khi nhận hàng)
  - Ví điện tử ZaloPay (mô phỏng)
- 🖼️ **Ảnh đại diện:** Upload ảnh lên Cloudinary, lưu vào DB
- 📜 **Lịch sử mua hàng:** Theo dõi đơn đã đặt

---

## 🛠️ Trang Admin

- 📦 Quản lý sản phẩm, loại sản phẩm
- 📇 Quản lý đơn hàng, khách hàng
- 📈 Thống kê doanh thu:
  - Theo hình thức thanh toán
  - Theo sản phẩm bán ra
- 📤 Gửi email xác nhận khi khách đặt hàng
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

### 🖥️ Cài đặt Backend (Spring Boot)

1. Tạo database `shopdb` trong MySQL:
   ```sql
   CREATE DATABASE shopdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
