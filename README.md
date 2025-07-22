📱 Giới Thiệu Dự Án - MyLocket

MyLocket là một ứng dụng mạng xã hội hiện đại trên Android, sử dụng Jetpack Compose, cho phép người dùng chia sẻ ảnh riêng tư với bạn bè, tương tác qua bình luận và trò chuyện thời gian thực.

Ứng dụng tập trung vào chia sẻ ảnh riêng tư trong mạng lưới bạn bè, giống như BeReal hoặc Locket Widget.

🌟 Tính Năng Nổi Bật

Giao diện hiện đại: Sử dụng Jetpack Compose & Material Design 3

Tính năng thời gian thực: Chat trực tiếp, thông báo tức thời

Bảo mật cao: Sử dụng Supabase với RLS (Row Level Security)

Kiến trúc MVVM: Mã sạch, dễ bảo trì

Chia sẻ trong mạng bạn bè: Ảnh chỉ chia sẻ với người thân thiết

🧱 Cấu Trúc Dự Án

Thư mục chính com.mylocket/ bao gồm:

screens/: Các màn hình chính như Đăng nhập, Trang chủ, Chat, Gửi ảnh, Hồ sơ

components/: Các thành phần UI tái sử dụng (bài viết, tin nhắn, ảnh)

service/: Kết nối backend Supabase (auth, database, lưu trữ, chat)

viewmodel/: Logic ứng dụng (bạn bè, đăng nhập, bài viết...)

theme/: Chủ đề giao diện (màu sắc, kiểu chữ...)

🔐 Xác Thực Người Dùng

Đăng ký bằng email & mật khẩu

Đăng nhập và giữ phiên làm việc

Tùy chỉnh hồ sơ (tên, email, ảnh đại diện)

Quên mật khẩu? → Gửi link đặt lại qua email

👥 Quản Lý Bạn Bè

Tìm bạn qua email hoặc ID

Gửi & nhận lời mời kết bạn

Trạng thái: Đã gửi, Đã nhận, Đã kết bạn

Cập nhật bạn bè thời gian thực

📸 Chia Sẻ Ảnh

Tích hợp CameraX chụp ảnh

Chia sẻ với bạn bè được chọn

Lưu ảnh trên Supabase Storage

Xem ảnh theo dạng cuộn dọc

💬 Trò Chuyện

Nhắn tin 1-1 giữa bạn bè

Chỉ bạn bè mới có thể nhắn tin

Hiển thị đã đọc/chưa đọc

Lưu lịch sử trò chuyện

💭 Bình Luận

Bình luận trực tiếp dưới bài viết

Cập nhật bình luận theo thời gian thực

Ghi nhận người bình luận

Hiển thị bình luận ngay trong feed

🧑 Quản Lý Hồ Sơ

Chỉnh sửa tên, email, avatar

Cài đặt tài khoản

Kiểm soát quyền riêng tư

🧭 Kiến Trúc MVVM

UI → Quan sát StateFlow từ ViewModel

ViewModel → Điều phối dữ liệu & logic

Service → Giao tiếp Supabase

RLS → Mỗi người chỉ thấy dữ liệu của họ

🔧 Cài Đặt

Clone dự án từ GitHub

Tạo Supabase project & điền thông tin vào SupabaseConfig.kt

Chạy app bằng Android Studio (hoặc dùng ./gradlew installDebug)

📦 Công Nghệ

Kotlin + Jetpack Compose

Supabase (PostgreSQL, Auth, Storage, Real-time)

Navigation Compose, Material 3

CameraX, Coil (ảnh), Accompanist (quyền)
