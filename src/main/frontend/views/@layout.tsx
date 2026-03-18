import { Outlet } from 'react-router';

export default function Layout() {
  return (
    <div className="cinema-app">
      <header className="cinema-header">
        <a href="/" className="cinema-logo">CineMax</a>
      </header>
      <main className="cinema-main">
        <Outlet />
      </main>
    </div>
  );
}
