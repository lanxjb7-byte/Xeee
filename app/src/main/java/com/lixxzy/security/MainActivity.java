package com.lixxzy.security;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(10, 10, 13);
    private static final int PANEL = Color.rgb(25, 25, 31);
    private static final int RED = Color.rgb(255, 48, 79);
    private static final int WHITE = Color.WHITE;
    private LinearLayout content;
    private TextView status;
    private String currentPackage = null;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        showHome();
    }

    @Override protected void onResume() {
        super.onResume();
        SharedPreferences p = getSharedPreferences("lixxzy_security", MODE_PRIVATE);
        String pending = p.getString("pending_package", null);
        if (pending != null && !pending.equals(getPackageName())) {
            p.edit().remove("pending_package").apply();
            scanPackage(pending, true);
        }
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(0, 6, 0, 6);
        return t;
    }

    private void add(LinearLayout parent, View v) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(10);
        parent.addView(v, lp);
    }

    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density); }

    private Button button(String label, int color, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(WHITE);
        b.setAllCaps(false);
        b.setTextSize(15);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        b.setOnClickListener(click);
        return b;
    }

    private void showHome() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(22), dp(30), dp(22), dp(24));
        scroll.addView(content);
        setContentView(scroll);

        ImageView logoView = new ImageView(this);
        logoView.setImageResource(com.lixxzy.security.R.drawable.lixxzy_logo);
        logoView.setAdjustViewBounds(true);
        logoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(-1, dp(145));
        logoParams.bottomMargin = dp(8);
        content.addView(logoView, logoParams);
        add(content, text("LIXXZY SECURITY", 25, WHITE, true));
        add(content, text("ANDROID PROTECTION CENTER", 11, RED, true));

        LinearLayout shield = new LinearLayout(this);
        shield.setOrientation(LinearLayout.VERTICAL);
        shield.setGravity(Gravity.CENTER);
        shield.setPadding(dp(18), dp(22), dp(18), dp(22));
        shield.setBackgroundTintList(android.content.res.ColorStateList.valueOf(PANEL));
        shield.setBackgroundColor(PANEL);
        TextView icon = text("🛡", 54, WHITE, true);
        icon.setGravity(Gravity.CENTER);
        shield.addView(icon);
        TextView heading = text("PERLINDUNGAN AKTIF", 18, WHITE, true);
        heading.setGravity(Gravity.CENTER);
        shield.addView(heading);
        TextView sub = text("Memantau pemberitahuan instalasi aplikasi", 12, 0xFFB8B8C0, false);
        sub.setGravity(Gravity.CENTER);
        shield.addView(sub);
        add(content, shield);

        add(content, text("AUTO SCAN APK", 18, WHITE, true));
        add(content, text("Saat Android memberi tahu bahwa aplikasi baru selesai dipasang, LIXXZY SECURITY akan memeriksa metadata dan permission yang dapat dibaca. Pemeriksaan ini bukan jaminan bebas malware.", 13, 0xFFB8B8C0, false));
        add(content, button("RIWAYAT / STATUS SCAN", RED, v -> showLatest()));
        add(content, button("PILIH APLIKASI UNTUK DIPERIKSA", PANEL, v -> {
            Intent i = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(i);
        }));
        add(content, text("CATATAN: Android membatasi akses antar-aplikasi. Aplikasi ini tidak menjalankan APK, tidak dapat melihat seluruh aktivitas aplikasi lain, dan tidak menghapus aplikasi tanpa konfirmasi pengguna.", 11, 0xFF92929A, false));
        showLatest();
    }

    private void showLatest() {
        SharedPreferences p = getSharedPreferences("lixxzy_security", MODE_PRIVATE);
        String pkg = p.getString("last_package", null);
        String result = p.getString("last_result", "Belum ada hasil pemindaian otomatis.");
        if (pkg == null) {
            add(content, text("HASIL TERAKHIR", 16, WHITE, true));
            add(content, text(result, 13, 0xFFB8B8C0, false));
        } else {
            add(content, text("HASIL TERAKHIR", 16, WHITE, true));
            add(content, text("Paket: " + pkg + "\n" + result, 13, 0xFFB8B8C0, false));
        }
    }

    private void scanPackage(String packageName, boolean automatic) {
        currentPackage = packageName;
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String appName = packageName;
            try {
                if (info.applicationInfo != null) appName = pm.getApplicationLabel(info.applicationInfo).toString();
            } catch (Exception ignored) {}

            StringBuilder findings = new StringBuilder();
            int risk = 0;
            String[] perms = info.requestedPermissions;
            if (perms != null) {
                for (String perm : perms) {
                    if (perm == null) continue;
                    if (perm.contains("REQUEST_INSTALL_PACKAGES") ||
                        perm.contains("SYSTEM_ALERT_WINDOW") ||
                        perm.contains("READ_SMS") ||
                        perm.contains("RECEIVE_BOOT_COMPLETED") ||
                        perm.contains("BIND_ACCESSIBILITY_SERVICE")) {
                        risk++;
                        findings.append("• Permission sensitif: ")
                                .append(perm.substring(perm.lastIndexOf('.') + 1)).append("\n");
                    }
                }
            }

            String verdict = risk >= 3 ? "PERLU DIPERIKSA" : "TIDAK ADA INDIKATOR DASAR";
            String riskText = risk >= 3 ? "Menengah — beberapa permission sensitif ditemukan" :
                    (risk > 0 ? "Perhatian — permission sensitif ditemukan" : "Belum ada indikator dasar yang cocok");
            String detail = "Aplikasi: " + appName + "\nPaket: " + packageName +
                    "\nVersi: " + (info.versionName == null ? "Tidak diketahui" : info.versionName) +
                    "\nHasil: " + verdict + "\nRisiko: " + riskText + "\n\n" +
                    (findings.length() == 0 ? "Tidak ditemukan permission sensitif dari daftar pemeriksaan dasar." : findings.toString()) +
                    "\nPemindaian ini tidak dapat memastikan ada/tidaknya virus atau ransomware.";
            SharedPreferences p = getSharedPreferences("lixxzy_security", MODE_PRIVATE);
            p.edit().putString("last_package", packageName)
                    .putString("last_result", detail)
                    .putLong("last_scan", System.currentTimeMillis()).apply();

            new AlertDialog.Builder(this)
                    .setTitle(automatic ? "AUTO SCAN SELESAI" : "HASIL PEMINDAIAN")
                    .setMessage(detail)
                    .setPositiveButton("OK", (d, w) -> showHome())
                    .setNeutralButton("COPOT APLIKASI", (d, w) -> uninstall(packageName))
                    .show();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("SCAN TIDAK SELESAI")
                    .setMessage("Android tidak memberikan akses informasi aplikasi ini. Detail: " + e.getMessage())
                    .setPositiveButton("OK", null).show();
        }
    }

    private void uninstall(String packageName) {
        try {
            Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
            startActivity(i);
        } catch (Exception e) {
            new AlertDialog.Builder(this).setMessage("Tidak dapat membuka halaman uninstall Android.")
                    .setPositiveButton("OK", null).show();
        }
    }
  }
          
